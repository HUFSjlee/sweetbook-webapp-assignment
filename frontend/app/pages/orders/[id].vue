<script setup lang="ts">
const route = useRoute()
const api = useApi()

const orderId = computed(() => route.params.id as string)
const { data, refresh } = await useAsyncData(
  () => `order-${orderId.value}`,
  () => api.getOrder(orderId.value)
)

const canceling = ref(false)
const errorMessage = ref('')

async function cancelOrder() {
  if (!data.value?.order) {
    return
  }

  try {
    canceling.value = true
    await api.cancelOrder(data.value.order.id)
    await refresh()
  } catch (error) {
    errorMessage.value = '주문 취소에 실패했습니다.'
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
        주문 상태와 SweetBook 응답 원문 일부를 함께 확인할 수 있습니다.
      </p>

      <div class="stat-grid">
        <div class="stat">
          <strong>{{ data.order.status }}</strong>
          <span class="muted">현재 상태</span>
        </div>
        <div class="stat">
          <strong>{{ data.order.estimatedPrice.toLocaleString() }} KRW</strong>
          <span class="muted">주문 금액</span>
        </div>
      </div>

      <div class="timeline" style="margin-top: 24px;">
        <div class="timeline__item">
          <strong>수령인</strong>
          <span class="muted">{{ data.order.recipientName }}</span>
        </div>
        <div class="timeline__item">
          <strong>배송지</strong>
          <span class="muted">{{ data.order.address }}</span>
        </div>
        <div class="timeline__item">
          <strong>연락처</strong>
          <span class="muted">{{ data.order.phone }}</span>
        </div>
      </div>

      <div v-if="errorMessage" class="error-box" style="margin-top: 18px;">
        {{ errorMessage }}
      </div>

      <div class="actions">
        <button class="danger-button" :disabled="canceling" @click="cancelOrder">
          {{ canceling ? 'Canceling...' : '주문 취소' }}
        </button>
        <NuxtLink class="ghost-button" to="/orders">목록으로</NuxtLink>
      </div>
    </section>

    <section class="panel">
      <span class="eyebrow">SweetBook Payload</span>
      <pre style="white-space: pre-wrap; word-break: break-word; margin: 16px 0 0;">{{ JSON.stringify(data.sweetBookOrder, null, 2) }}</pre>
    </section>
  </div>
</template>
