export interface ApiEnvelope<T> {
  success: boolean
  data: T
  message?: string | null
}

export interface Travel {
  id: string
  title: string
  description?: string | null
  startDate?: string | null
  endDate?: string | null
  mood?: string | null
  status: string
  bookUid?: string | null
  coverImageUrl?: string | null
  createdAt: string
  updatedAt: string
}

export interface Photo {
  id: string
  travelId: string
  imageUrl: string
  storagePath: string
  comment?: string | null
  location?: string | null
  takenDate?: string | null
  sortOrder: number
  createdAt: string
}

export interface BookSpec {
  bookSpecUid: string
  name: string
  pageMin: number
  pageMax: number
  pageDefault: number
  pageIncrement: number
  sandboxPriceBase?: number | null
  sandboxPricePerIncrement?: number | null
}

export interface TemplateListItem {
  templateUid: string
  templateName: string
  templateKind: string
  theme: string
  bookSpecUid: string
  thumbnails?: {
    layout?: string
  }
}

export interface TemplateBundle {
  theme: string
  cover?: TemplateListItem
  content?: TemplateListItem
  blank?: TemplateListItem
  templates: TemplateListItem[]
}

export interface BuildResult {
  travelId: string
  bookUid: string
  pageCount: number
  finalizedAt?: string | null
}

export interface OrderEstimate {
  travelId: string
  estimatedPrice: number
  rawResponse: Record<string, unknown>
}

export interface Order {
  id: string
  travelId: string
  orderUid: string
  status: string
  recipientName: string
  address: string
  phone: string
  estimatedPrice: number
  createdAt: string
}

export interface OrderDetail {
  order: Order
  sweetBookOrder: Record<string, unknown>
}
