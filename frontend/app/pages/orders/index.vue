<script setup lang="ts">
const api = useApi()
const { data: orders, refresh, error } = await useAsyncData('orders', () => api.getOrders())
</script>

<template>
  <section class="panel" style="display: grid; gap: 20px;">
    <div class="split-header">
      <div>
        <span class="eyebrow">Orders</span>
        <h1 class="section-title">주문 내역</h1>
        <p class="muted">
          생성된 주문과 SweetBook 주문 번호를 한 화면에서 확인합니다.
        </p>
      </div>
      <button class="ghost-button" @click="refresh()">새로고침</button>
    </div>

    <div v-if="error" class="error-box">
      주문 목록 조회에 실패했습니다.
    </div>

    <div v-else-if="orders?.length" class="content-grid">
      <article v-for="order in orders" :key="order.id" class="card">
        <div class="meta-row">
          <span class="status-badge">{{ order.status }}</span>
          <span>{{ order.createdAt.slice(0, 10) }}</span>
        </div>
        <h3 style="margin: 14px 0 8px;">{{ order.orderUid }}</h3>
        <p class="muted">
          {{ order.recipientName }} · {{ order.address }}
        </p>
        <div class="chip-row" style="margin-top: 16px;">
          <span class="chip">{{ order.estimatedPrice.toLocaleString() }} KRW</span>
          <span class="chip">Travel {{ order.travelId.slice(0, 8) }}</span>
        </div>
        <div class="actions">
          <NuxtLink class="button" :to="`/orders/${order.id}`">상세 보기</NuxtLink>
        </div>
      </article>
    </div>

    <EmptyState
      v-else
      title="아직 생성된 주문이 없습니다"
      description="여행 프로젝트에서 포토북을 빌드하고 주문 화면으로 이동하면 여기서 상태를 추적할 수 있습니다."
      action-label="여행 프로젝트 보기"
      action-to="/travels"
    />
  </section>
</template>
