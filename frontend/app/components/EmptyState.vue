<script setup lang="ts">
defineProps<{
  title: string
  description: string
  actionLabel?: string
  actionTo?: string
}>()
</script>

<template>
  <div class="empty-state">
    <h3 style="margin-bottom: 10px;">{{ title }}</h3>
    <p class="muted" style="max-width: 520px; margin: 0 auto;">
      {{ description }}
    </p>
    <div v-if="actionLabel && actionTo" class="actions" style="justify-content: center;">
      <NuxtLink class="button" :to="actionTo">{{ actionLabel }}</NuxtLink>
    </div>
  </div>
</template>
