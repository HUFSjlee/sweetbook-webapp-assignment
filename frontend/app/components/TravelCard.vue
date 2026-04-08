<script setup lang="ts">
import type { Travel } from '~/app/types/api'

const props = defineProps<{
  travel: Travel
}>()

const { formatStatus } = useDisplay()
</script>

<template>
  <article class="card home-project-card">
    <div class="meta-row">
      <span class="status-badge">{{ formatStatus(props.travel.status) }}</span>
      <span>{{ props.travel.startDate || '날짜 미정' }}</span>
    </div>

    <h3 class="home-project-card__title">
      {{ props.travel.title }}
    </h3>

    <p class="muted">
      {{ props.travel.description || '사진과 메모를 모아 포토북으로 정리하는 여행 프로젝트입니다.' }}
    </p>

    <div class="chip-row" style="margin-top: 16px;">
      <span class="chip">{{ props.travel.mood || '기록중' }}</span>
      <span class="chip">{{ props.travel.bookUid ? '책 생성 완료' : '초안 상태' }}</span>
    </div>

    <div class="actions">
      <NuxtLink class="button" :to="`/travels/${props.travel.id}`">
        프로젝트 열기
      </NuxtLink>
      <NuxtLink class="ghost-button" :to="`/travels/${props.travel.id}/preview`">
        프리뷰 보기
      </NuxtLink>
    </div>
  </article>
</template>
