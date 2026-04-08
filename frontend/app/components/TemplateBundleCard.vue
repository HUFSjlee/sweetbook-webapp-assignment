<script setup lang="ts">
import type { TemplateBundle } from '~/app/types/api'

defineProps<{
  bundle: TemplateBundle
  active: boolean
}>()
</script>

<template>
  <button class="template-card" :class="{ 'is-active': active }" style="text-align: left; width: 100%;">
    <img :src="bundle.cover?.thumbnails?.layout || bundle.content?.thumbnails?.layout" :alt="bundle.theme">
    <div class="template-card__body">
      <div class="meta-row">
        <span class="chip">{{ bundle.theme }}</span>
        <span>{{ bundle.templates.length }} templates</span>
      </div>
      <h3 style="margin: 12px 0 8px;">{{ bundle.theme }}</h3>
      <p class="muted">
        {{ bundle.cover?.templateName }} + {{ bundle.content?.templateName }}
      </p>
    </div>
  </button>
</template>
