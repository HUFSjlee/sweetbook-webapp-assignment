import type {
  ApiEnvelope,
  BookSpec,
  BuildResult,
  Order,
  OrderDetail,
  OrderEstimate,
  Photo,
  TemplateBundle,
  TemplateListItem,
  Travel
} from '~/app/types/api'

function groupTemplates(items: TemplateListItem[]) {
  const groups = new Map<string, TemplateBundle>()

  for (const item of items) {
    const current = groups.get(item.theme) ?? {
      theme: item.theme,
      cover: undefined,
      content: undefined,
      blank: undefined,
      templates: []
    }

    current.templates.push(item)

    if (item.templateKind === 'cover' && !current.cover) {
      current.cover = item
    }

    if (item.templateKind === 'content') {
      const isBlank = item.templateName.includes('빈내지')
      if (isBlank && !current.blank) {
        current.blank = item
      }

      if (!isBlank && !current.content) {
        current.content = item
      }
    }

    groups.set(item.theme, current)
  }

  return [...groups.values()].filter((bundle) => bundle.cover && bundle.content)
}

export function useApi() {
  const config = useRuntimeConfig()

  async function request<T>(path: string, options: Parameters<typeof $fetch<ApiEnvelope<T>>>[1] = {}) {
    const response = await $fetch<ApiEnvelope<T>>(path, {
      baseURL: config.public.apiBase,
      ...options
    })
    return response.data
  }

  return {
    request,
    getTravels: () => request<Travel[]>('/travels'),
    getTravel: (travelId: string) => request<Travel>(`/travels/${travelId}`),
    createTravel: (payload: Partial<Travel>) => request<Travel>('/travels', { method: 'POST', body: payload }),
    updateTravel: (travelId: string, payload: Partial<Travel>) =>
      request<Travel>(`/travels/${travelId}`, { method: 'PUT', body: payload }),
    deleteTravel: (travelId: string) => request<void>(`/travels/${travelId}`, { method: 'DELETE' }),
    getPhotos: (travelId: string) => request<Photo[]>(`/travels/${travelId}/photos`),
    uploadPhoto: (travelId: string, formData: FormData) =>
      request<Photo>(`/travels/${travelId}/photos`, { method: 'POST', body: formData }),
    updatePhoto: (photoId: string, payload: Partial<Photo>) =>
      request<Photo>(`/photos/${photoId}`, { method: 'PUT', body: payload }),
    deletePhoto: (photoId: string) => request<void>(`/photos/${photoId}`, { method: 'DELETE' }),
    reorderPhotos: (travelId: string, payload: Array<{ photoId: string; sortOrder: number }>) =>
      request<Photo[]>(`/travels/${travelId}/photos/reorder`, { method: 'PUT', body: payload }),
    getBookSpecs: () => request<BookSpec[]>('/bookspecs'),
    getTemplateBundles: async (bookSpecUid: string) => {
      const result = await request<{ templates: TemplateListItem[]; pagination: Record<string, unknown> } | TemplateListItem[]>(
        '/templates',
        { query: { bookSpecUid } }
      )

      const templates = Array.isArray(result) ? result : result.templates
      return groupTemplates(templates)
    },
    buildBook: (travelId: string, payload: Record<string, unknown>) =>
      request<BuildResult>(`/travels/${travelId}/build`, { method: 'POST', body: payload }),
    estimateOrder: (travelId: string) =>
      request<OrderEstimate>(`/travels/${travelId}/estimate`, { method: 'POST', body: {} }),
    createOrder: (travelId: string, payload: Record<string, unknown>) =>
      request<Order>(`/travels/${travelId}/order`, { method: 'POST', body: payload }),
    getOrders: () => request<Order[]>('/orders'),
    getOrder: (orderId: string) => request<OrderDetail>(`/orders/${orderId}`),
    cancelOrder: (orderId: string) => request<Order>(`/orders/${orderId}/cancel`, { method: 'POST' })
  }
}
