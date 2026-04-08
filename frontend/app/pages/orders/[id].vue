<script setup lang="ts">
const route = useRoute()
const api = useApi()
const { formatStatus } = useDisplay()

const orderId = computed(() => route.params.id as string)
const { data, refresh } = await useAsyncData(
  () => `order-${orderId.value}`,
  () => api.getOrder(orderId.value)
)

const canceling = ref(false)
const errorMessage = ref('')
const cancelNotice = 'SweetBook sandbox orders are currently treated as non-cancelable in this app.'

async function reloadOrder() {
  errorMessage.value = ''
  await refresh()
}

async function cancelOrder() {
  if (!data.value?.order) {
    return
  }

  try {
    canceling.value = true
    errorMessage.value = ''
    await api.cancelOrder(data.value.order.id)
    await refresh()
  } catch (error) {
    errorMessage.value = 'Sandbox environment does not allow this order to be canceled.'
    console.error(error)
  } finally {
    canceling.value = false
  }
}
</script>

<template>
  <div v-if="data?.order" class="two-column">
    <section class="glass-panel">
      <span class="eyebrow">Order Detail</span>
      <h1 class="section-title">{{ data.order.orderUid }}</h1>
      <p class="muted">
        Saved order data and the latest SweetBook payload are shown together.
      </p>

      <div class="stat-grid">
        <div class="stat">
          <strong>{{ formatStatus(data.order.status) }}</strong>
          <span class="muted">Current status</span>
        </div>
        <div class="stat">
          <strong>{{ data.order.estimatedPrice.toLocaleString() }} KRW</strong>
          <span class="muted">Estimated total</span>
        </div>
      </div>

      <div class="timeline" style="margin-top: 24px;">
        <div class="timeline__item">
          <strong>Recipient</strong>
          <span class="muted">{{ data.order.recipientName }}</span>
        </div>
        <div class="timeline__item">
          <strong>Address</strong>
          <span class="muted">{{ data.order.address }}</span>
        </div>
        <div class="timeline__item">
          <strong>Phone</strong>
          <span class="muted">{{ data.order.phone }}</span>
        </div>
      </div>

      <div class="panel" style="margin-top: 20px; padding: 18px;">
        <strong style="display: block; margin-bottom: 8px;">Cancellation</strong>
        <p class="muted">
          {{ cancelNotice }}
        </p>
      </div>

      <div v-if="errorMessage" class="error-box" style="margin-top: 18px;">
        {{ errorMessage }}
      </div>

      <div class="actions">
        <button class="ghost-button" :disabled="canceling" @click="reloadOrder">
          Refresh detail
        </button>
        <button class="danger-button" :disabled="true" @click="cancelOrder">
          Cancel unavailable
        </button>
        <NuxtLink class="ghost-button" to="/orders">Back to orders</NuxtLink>
      </div>
    </section>

    <section class="panel">
      <span class="eyebrow">SweetBook Payload</span>
      <pre style="white-space: pre-wrap; word-break: break-word; margin: 16px 0 0;">{{ JSON.stringify(data.sweetBookOrder, null, 2) }}</pre>
    </section>
  </div>
</template>
