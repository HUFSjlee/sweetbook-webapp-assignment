<script setup lang="ts">
const route = useRoute()
const router = useRouter()
const api = useApi()

const travelId = computed(() => route.params.id as string)

const { data: travel } = await useAsyncData(
  () => `order-travel-${travelId.value}`,
  () => api.getTravel(travelId.value)
)

const estimate = ref<number | null>(null)
const estimating = ref(false)
const ordering = ref(false)
const resultMessage = ref('')
const errorMessage = ref('')

const form = reactive({
  recipientName: 'Test User',
  recipientPhone: '01012345678',
  postalCode: '06236',
  address1: 'Seoul Test-ro 1',
  address2: '101-ho',
  memo: 'leave at door'
})

async function loadEstimate() {
  try {
    estimating.value = true
    errorMessage.value = ''
    const result = await api.estimateOrder(travelId.value)
    estimate.value = result.estimatedPrice
  } catch (error) {
    errorMessage.value = 'Estimate lookup failed. Build the SweetBook project first.'
    console.error(error)
  } finally {
    estimating.value = false
  }
}

await loadEstimate()

async function submitOrder() {
  if (estimate.value == null) {
    return
  }

  try {
    ordering.value = true
    errorMessage.value = ''
    resultMessage.value = ''

    const order = await api.createOrder(travelId.value, {
      ...form,
      estimatedPrice: estimate.value
    })

    resultMessage.value = `Order ${order.orderUid} was created.`
    await router.push(`/orders/${order.id}`)
  } catch (error) {
    errorMessage.value = 'Order creation failed.'
    console.error(error)
  } finally {
    ordering.value = false
  }
}
</script>

<template>
  <div v-if="travel" class="two-column">
    <section class="glass-panel">
      <span class="eyebrow">Order Checkout</span>
      <h1 class="section-title">{{ travel.title }} checkout</h1>
      <p class="muted">
        The estimate comes from SweetBook and the order is created through the backend.
      </p>

      <div class="stat-grid">
        <div class="stat">
          <strong>{{ estimate == null ? '...' : `${estimate.toLocaleString()} KRW` }}</strong>
          <span class="muted">Current estimate</span>
        </div>
        <div class="stat">
          <strong>{{ travel.bookUid || 'N/A' }}</strong>
          <span class="muted">Connected book UID</span>
        </div>
      </div>

      <div class="actions">
        <button class="ghost-button" :disabled="estimating" @click="loadEstimate">
          {{ estimating ? 'Refreshing...' : 'Refresh estimate' }}
        </button>
      </div>
    </section>

    <section class="panel">
      <div v-if="errorMessage" class="error-box">
        {{ errorMessage }}
      </div>
      <div v-if="resultMessage" class="success-box">
        {{ resultMessage }}
      </div>

      <form class="form-grid" @submit.prevent="submitOrder">
        <div class="field">
          <label>Recipient</label>
          <input v-model="form.recipientName" required>
        </div>
        <div class="field">
          <label>Phone</label>
          <input v-model="form.recipientPhone" required>
        </div>
        <div class="field">
          <label>Postal code</label>
          <input v-model="form.postalCode" required>
        </div>
        <div class="field">
          <label>Address line 1</label>
          <input v-model="form.address1" required>
        </div>
        <div class="field">
          <label>Address line 2</label>
          <input v-model="form.address2">
        </div>
        <div class="field">
          <label>Delivery memo</label>
          <textarea v-model="form.memo" />
        </div>

        <div class="actions">
          <button class="button" type="submit" :disabled="ordering || estimate == null">
            {{ ordering ? 'Ordering...' : 'Create order' }}
          </button>
          <NuxtLink class="ghost-button" :to="`/travels/${travel.id}/preview`">Back to preview</NuxtLink>
        </div>
      </form>
    </section>
  </div>
</template>
