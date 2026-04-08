<script setup lang="ts">
const api = useApi()
const { data: travels, pending, error, refresh } = await useAsyncData('travels', () => api.getTravels())
</script>

<template>
  <section class="panel" style="display: grid; gap: 20px;">
    <div class="split-header">
      <div>
        <span class="eyebrow">Travel Projects</span>
        <h1 class="section-title">여행 프로젝트 아카이브</h1>
        <p class="muted">
          프로젝트를 열면 사진 정리, 코멘트 작성, 포토북 빌드와 주문까지 한 흐름으로 이어집니다.
        </p>
      </div>

      <div class="actions">
        <button class="ghost-button" @click="refresh()">새로고침</button>
        <NuxtLink class="button" to="/travels/new">새 여행 만들기</NuxtLink>
      </div>
    </div>

    <div v-if="pending" class="notice">
      여행 목록을 불러오는 중입니다.
    </div>

    <div v-if="error" class="error-box">
      여행 목록 조회에 실패했습니다.
    </div>

    <div v-else-if="travels?.length" class="content-grid">
      <TravelCard v-for="travel in travels" :key="travel.id" :travel="travel" />
    </div>

    <EmptyState
      v-else
      title="여행 프로젝트가 비어 있습니다"
      description="여행 제목과 기간만 입력해도 바로 사진 업로드와 프리뷰 흐름을 시작할 수 있습니다."
      action-label="프로젝트 만들기"
      action-to="/travels/new"
    />
  </section>
</template>
