<script setup lang="ts">
import type { BookSpec, TemplateBundle } from '~/app/types/api'

const route = useRoute()
const api = useApi()
const travelId = computed(() => route.params.id as string)

const travel = await api.getTravel(travelId.value)
const photos = await api.getPhotos(travelId.value)
const allBookSpecs = await api.getBookSpecs()

const availableBookSpecs = allBookSpecs.filter((spec) => spec.pageDefault > 0 && Boolean(spec.name?.trim()))
const selectedBookSpecUid = ref(availableBookSpecs[0]?.bookSpecUid || '')
const templateBundles = ref<TemplateBundle[]>(
  selectedBookSpecUid.value ? await api.getTemplateBundles(selectedBookSpecUid.value) : []
)
const selectedTheme = ref(templateBundles.value[0]?.theme || '')
const buildResultMessage = ref('')
const buildError = ref('')
const building = ref(false)

const selectedBundle = computed(() => templateBundles.value.find((bundle) => bundle.theme === selectedTheme.value))

async function selectBookSpec(bookSpecUid: string) {
  selectedBookSpecUid.value = bookSpecUid
  templateBundles.value = await api.getTemplateBundles(bookSpecUid)
  selectedTheme.value = templateBundles.value[0]?.theme || ''
}

async function buildBook() {
  if (!selectedBundle.value || !selectedBundle.value.cover || !selectedBundle.value.content) {
    return
  }

  try {
    building.value = true
    buildError.value = ''
    buildResultMessage.value = ''

    const result = await api.buildBook(travelId.value, {
      bookSpecUid: selectedBookSpecUid.value,
      coverTemplateUid: selectedBundle.value.cover.templateUid,
      contentTemplateUid: selectedBundle.value.content.templateUid,
      blankTemplateUid: selectedBundle.value.blank?.templateUid,
      coverTitle: travel.title
    })

    buildResultMessage.value = `Book ${result.bookUid} generated with ${result.pageCount} pages.`
  } catch (error) {
    buildError.value = 'Failed to build the SweetBook output. Check templates and page data.'
    console.error(error)
  } finally {
    building.value = false
  }
}
</script>

<template>
  <div class="detail-grid">
    <div style="display: grid; gap: 24px;">
      <section class="glass-panel">
        <span class="eyebrow">Build Preview</span>
        <h1 class="section-title">{{ travel.title }}</h1>
        <p class="muted">
          Choose a book spec and theme bundle, then generate the printable SweetBook output.
          Current photo count: {{ photos.length }}.
        </p>

        <div v-if="buildResultMessage" class="success-box" style="margin-top: 18px;">
          {{ buildResultMessage }}
        </div>

        <div v-if="buildError" class="error-box" style="margin-top: 18px;">
          {{ buildError }}
        </div>
      </section>

      <section class="panel">
        <div class="split-header">
          <div>
            <span class="eyebrow">Book Spec</span>
            <h2 class="section-title">Choose Format</h2>
          </div>
        </div>

        <div v-if="availableBookSpecs.length" class="template-grid">
          <button
            v-for="spec in availableBookSpecs"
            :key="spec.bookSpecUid"
            class="card"
            :class="{ 'is-active': selectedBookSpecUid === spec.bookSpecUid }"
            style="text-align: left; min-width: 220px;"
            @click="selectBookSpec(spec.bookSpecUid)"
          >
            <div class="meta-row">
              <span class="chip">{{ spec.bookSpecUid }}</span>
            </div>
            <h3 style="margin: 12px 0 8px;">{{ spec.name }}</h3>
            <p class="muted">
              Default {{ spec.pageDefault }}p, min {{ spec.pageMin }}p, increment {{ spec.pageIncrement }}p
            </p>
          </button>
        </div>

        <EmptyState
          v-else
          title="No formats available"
          description="SweetBook did not return a usable product spec."
        />
      </section>

      <section class="panel">
        <div class="split-header">
          <div>
            <span class="eyebrow">Theme Bundle</span>
            <h2 class="section-title">Choose Theme</h2>
          </div>
        </div>

        <div v-if="templateBundles.length" class="content-grid">
          <button
            v-for="bundle in templateBundles"
            :key="bundle.theme"
            style="border: none; background: transparent; padding: 0;"
            @click="selectedTheme = bundle.theme"
          >
            <TemplateBundleCard :bundle="bundle" :active="bundle.theme === selectedTheme" />
          </button>
        </div>

        <EmptyState
          v-else
          title="No themes available"
          description="The selected format did not produce a usable cover/content bundle."
        />
      </section>
    </div>

    <aside style="display: grid; gap: 24px;">
      <section class="panel">
        <span class="eyebrow">Selected Output</span>
        <div class="preview-book" style="margin-top: 14px;">
          <div class="preview-book__cover">
            <div class="preview-book__meta">
              <span class="chip">{{ selectedBundle?.theme || 'No theme selected' }}</span>
              <strong>{{ travel.title }}</strong>
              <p class="muted">
                {{ travel.description || 'Your travel story will be shaped into a printable photo book.' }}
              </p>
            </div>
            <img
              :src="photos[0]?.imageUrl || selectedBundle?.cover?.thumbnails?.layout"
              alt="Selected preview"
            >
          </div>
        </div>
      </section>

      <section class="panel">
        <span class="eyebrow">Build Action</span>
        <p class="muted" style="margin-top: 12px;">
          Current selection:
          {{ selectedBundle?.cover?.templateName || 'cover n/a' }}
          /
          {{ selectedBundle?.content?.templateName || 'content n/a' }}
        </p>
        <div class="actions">
          <button class="button" :disabled="building || !selectedBundle || !photos.length" @click="buildBook">
            {{ building ? 'Building...' : 'Generate SweetBook Output' }}
          </button>
          <NuxtLink class="ghost-button" :to="`/travels/${travel.id}/order`">
            Go To Order
          </NuxtLink>
        </div>
      </section>
    </aside>
  </div>
</template>
