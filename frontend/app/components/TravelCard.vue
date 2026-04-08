<script setup lang="ts">
import type { Travel } from '~/app/types/api'

defineProps<{
  travel: Travel
}>()
</script>

<template>
  <article class="card">
    <div class="meta-row">
      <span class="status-badge">{{ travel.status }}</span>
      <span>{{ travel.startDate || 'Date TBD' }}</span>
    </div>

    <h3 style="margin: 14px 0 10px; font-size: 1.35rem;">
      {{ travel.title }}
    </h3>

    <p class="muted">
      {{ travel.description || '사진, 메모, 주문 흐름까지 이어지는 여행 포토북 프로젝트입니다.' }}
    </p>

    <div class="chip-row" style="margin-top: 16px;">
      <span class="chip">{{ travel.mood || 'Open mood' }}</span>
      <span class="chip">{{ travel.bookUid ? 'Book linked' : 'Draft only' }}</span>
    </div>

    <div class="actions">
      <NuxtLink class="button" :to="`/travels/${travel.id}`">
        Open project
      </NuxtLink>
      <NuxtLink class="ghost-button" :to="`/travels/${travel.id}/preview`">
        Preview book
      </NuxtLink>
    </div>
  </article>
</template>
