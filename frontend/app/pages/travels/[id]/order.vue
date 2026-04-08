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
  recipientName: '',
  recipientPhone: '',
  postalCode: '',
  address1: '',
  address2: '',
  memo: ''
})

async function loadEstimate() {
  try {
    estimating.value = true
    const result = await api.estimateOrder(travelId.value)
    estimate.value = result.estimatedPrice
  } catch (error) {
    errorMessage.value = '견적 조회에 실패했습니다. 먼저 포토북을 생성했는지 확인해 주세요.'
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

    resultMessage.value = `주문 ${order.orderUid} 이 생성되었습니다.`
    await router.push(`/orders/${order.id}`)
  } catch (error) {
    errorMessage.value = '주문 생성에 실패했습니다.'
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
      <h1 class="section-title">{{ travel.title }} 주문하기</h1>
      <p class="muted">
        SweetBook에서 계산한 견적을 확인한 뒤 배송 정보를 입력하면 주문이 생성됩니다.
      </p>

      <div class="stat-grid">
        <div class="stat">
          <strong>{{ estimate == null ? '...' : `${estimate.toLocaleString()} KRW` }}</strong>
          <span class="muted">현재 견적</span>
        </div>
        <div class="stat">
          <strong>{{ travel.bookUid || 'N/A' }}</strong>
          <span class="muted">연결된 책 UID</span>
        </div>
      </div>

      <div class="actions">
        <button class="ghost-button" :disabled="estimating" @click="loadEstimate">
          {{ estimating ? 'Refreshing...' : '견적 다시 계산' }}
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
          <label>수령인</label>
          <input v-model="form.recipientName" required>
        </div>
        <div class="field">
          <label>연락처</label>
          <input v-model="form.recipientPhone" required>
        </div>
        <div class="field">
          <label>우편번호</label>
          <input v-model="form.postalCode" required>
        </div>
        <div class="field">
          <label>기본 주소</label>
          <input v-model="form.address1" required>
        </div>
        <div class="field">
          <label>상세 주소</label>
          <input v-model="form.address2">
        </div>
        <div class="field">
          <label>배송 메모</label>
          <textarea v-model="form.memo" />
        </div>

        <div class="actions">
          <button class="button" type="submit" :disabled="ordering || estimate == null">
            {{ ordering ? 'Ordering...' : '주문 생성' }}
          </button>
          <NuxtLink class="ghost-button" :to="`/travels/${travel.id}/preview`">프리뷰로 돌아가기</NuxtLink>
        </div>
      </form>
    </section>
  </div>
</template>
