<script setup lang="ts">
import type { BookSpec, TemplateBundle } from '~/app/types/api'

const route = useRoute()
const api = useApi()
const travelId = computed(() => route.params.id as string)

const { data: travel } = await useAsyncData(
  () => `preview-travel-${travelId.value}`,
  () => api.getTravel(travelId.value)
)

const { data: photos } = await useAsyncData(
  () => `preview-photos-${travelId.value}`,
  () => api.getPhotos(travelId.value)
)

const { data: bookSpecs } = await useAsyncData('book-specs', () => api.getBookSpecs())

const selectedBookSpecUid = ref('')
const templateBundles = ref<TemplateBundle[]>([])
const selectedTheme = ref('')
const buildResultMessage = ref('')
const buildError = ref('')
const building = ref(false)

watch(
  bookSpecs,
  (value) => {
    if (value?.length && !selectedBookSpecUid.value) {
      selectedBookSpecUid.value = value[0].bookSpecUid
    }
  },
  { immediate: true }
)

watch(
  selectedBookSpecUid,
  async (value) => {
    if (!value) {
      return
    }
    templateBundles.value = await api.getTemplateBundles(value)
    selectedTheme.value = templateBundles.value[0]?.theme || ''
  },
  { immediate: true }
)

const selectedBundle = computed(() => templateBundles.value.find((bundle) => bundle.theme === selectedTheme.value))

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
      coverTitle: travel.value?.title
    })

    buildResultMessage.value = `Book ${result.bookUid} generated with ${result.pageCount} pages.`
  } catch (error) {
    buildError.value = '포토북 생성에 실패했습니다. 사진 수와 템플릿 구성을 다시 확인해 주세요.'
    console.error(error)
  } finally {
    building.value = false
  }
}
</script>

<template>
  <div v-if="travel" class="detail-grid">
    <div style="display: grid; gap: 24px;">
      <section class="glass-panel">
        <span class="eyebrow">Build Preview</span>
        <h1 class="section-title">{{ travel.title }}</h1>
        <p class="muted">
          판형과 테마를 고른 뒤 SweetBook 출력용 책을 실제로 생성합니다.
          현재 선택된 사진 수는 {{ photos?.length || 0 }}장입니다.
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
            <h2 class="section-title">판형 선택</h2>
          </div>
        </div>

        <div class="template-grid">
          <button
            v-for="spec in bookSpecs"
            :key="spec.bookSpecUid"
            class="card"
            :class="{ 'is-active': selectedBookSpecUid === spec.bookSpecUid }"
            style="text-align: left; min-width: 220px;"
            @click="selectedBookSpecUid = spec.bookSpecUid"
          >
            <div class="meta-row">
              <span class="chip">{{ spec.bookSpecUid }}</span>
            </div>
            <h3 style="margin: 12px 0 8px;">{{ spec.name }}</h3>
            <p class="muted">
              기본 {{ spec.pageDefault }}p · 최소 {{ spec.pageMin }}p · 증분 {{ spec.pageIncrement }}p
            </p>
          </button>
        </div>
      </section>

      <section class="panel">
        <div class="split-header">
          <div>
            <span class="eyebrow">Theme Bundle</span>
            <h2 class="section-title">출력 테마 선택</h2>
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
          title="사용 가능한 테마가 없습니다"
          description="선택한 판형에 대한 템플릿을 불러오지 못했습니다."
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
                {{ travel.description || '감정과 장면을 인쇄용 스토리북으로 정리합니다.' }}
              </p>
            </div>
            <img
              :src="photos?.[0]?.imageUrl || selectedBundle?.cover?.thumbnails?.layout"
              alt="Selected preview"
            >
          </div>
        </div>
      </section>

      <section class="panel">
        <span class="eyebrow">Build Action</span>
        <p class="muted" style="margin-top: 12px;">
          현재 선택:
          {{ selectedBundle?.cover?.templateName || 'cover n/a' }}
          /
          {{ selectedBundle?.content?.templateName || 'content n/a' }}
        </p>
        <div class="actions">
          <button class="button" :disabled="building || !selectedBundle || !(photos?.length)" @click="buildBook">
            {{ building ? 'Building...' : 'SweetBook 포토북 생성' }}
          </button>
          <NuxtLink class="ghost-button" :to="`/travels/${travel.id}/order`">
            주문 화면으로
          </NuxtLink>
        </div>
      </section>
    </aside>
  </div>
</template>
