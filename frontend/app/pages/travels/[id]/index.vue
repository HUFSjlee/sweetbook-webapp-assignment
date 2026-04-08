<script setup lang="ts">
import type { Photo } from '~/app/types/api'

const route = useRoute()
const api = useApi()
const { formatStatus } = useDisplay()
const travelId = computed(() => route.params.id as string)

const { data: travel } = await useAsyncData(
  () => `travel-${travelId.value}`,
  () => api.getTravel(travelId.value)
)

const { data: photos, refresh: refreshPhotos } = await useAsyncData(
  () => `travel-photos-${travelId.value}`,
  () => api.getPhotos(travelId.value)
)

const uploadForm = reactive({
  comment: '',
  location: ''
})

const selectedFile = ref<File | null>(null)
const uploadError = ref('')
const uploadPending = ref(false)
const savePendingId = ref<string | null>(null)

function onFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  selectedFile.value = target.files?.[0] || null
}

async function upload() {
  if (!selectedFile.value) {
    uploadError.value = '업로드할 이미지를 먼저 선택해 주세요.'
    return
  }

  try {
    uploadPending.value = true
    uploadError.value = ''

    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('comment', uploadForm.comment)
    formData.append('location', uploadForm.location)

    await api.uploadPhoto(travelId.value, formData)
    selectedFile.value = null
    uploadForm.comment = ''
    uploadForm.location = ''
    await refreshPhotos()
  } catch (error) {
    uploadError.value = '사진 업로드에 실패했습니다.'
    console.error(error)
  } finally {
    uploadPending.value = false
  }
}

async function savePhoto(photo: Photo) {
  try {
    savePendingId.value = photo.id
    await api.updatePhoto(photo.id, {
      comment: photo.comment,
      location: photo.location,
      takenDate: photo.takenDate
    })
    await refreshPhotos()
  } catch (error) {
    console.error(error)
  } finally {
    savePendingId.value = null
  }
}

async function removePhoto(photoId: string) {
  await api.deletePhoto(photoId)
  await refreshPhotos()
}

async function movePhoto(photoId: string, direction: -1 | 1) {
  if (!photos.value?.length) {
    return
  }

  const ordered = [...photos.value].sort((a, b) => a.sortOrder - b.sortOrder)
  const index = ordered.findIndex((photo) => photo.id === photoId)
  const targetIndex = index + direction
  if (index < 0 || targetIndex < 0 || targetIndex >= ordered.length) {
    return
  }

  const next = [...ordered]
  const [moved] = next.splice(index, 1)
  next.splice(targetIndex, 0, moved)

  await api.reorderPhotos(
    travelId.value,
    next.map((photo, order) => ({
      photoId: photo.id,
      sortOrder: order + 1
    }))
  )

  await refreshPhotos()
}
</script>

<template>
  <div v-if="travel" class="detail-grid">
    <div style="display: grid; gap: 24px;">
      <section class="glass-panel">
        <div class="meta-row">
          <span class="status-badge">{{ formatStatus(travel.status) }}</span>
          <span>{{ travel.startDate || 'No start date' }} ~ {{ travel.endDate || 'No end date' }}</span>
        </div>
        <h1 class="section-title" style="margin-top: 16px;">{{ travel.title }}</h1>
        <p class="muted">
          {{ travel.description || '이 프로젝트의 사진과 메모를 모아 한 권의 포토북으로 정리합니다.' }}
        </p>
        <div class="chip-row" style="margin-top: 18px;">
          <span class="chip">Mood: {{ travel.mood || 'Open' }}</span>
          <span class="chip">{{ photos?.length || 0 }} photos</span>
          <span class="chip">{{ travel.bookUid ? `Book ${travel.bookUid}` : 'No book built yet' }}</span>
        </div>
        <div class="actions">
          <NuxtLink class="button" :to="`/travels/${travel.id}/preview`">포토북 프리뷰로 이동</NuxtLink>
          <NuxtLink class="ghost-button" :to="`/travels/${travel.id}/order`">주문 화면</NuxtLink>
        </div>
      </section>

      <section class="panel">
        <div class="split-header">
          <div>
            <span class="eyebrow">Photo Board</span>
            <h2 class="section-title">사진과 메모 정리</h2>
          </div>
        </div>

        <div class="dropzone">
          <div v-if="uploadError" class="error-box">
            {{ uploadError }}
          </div>

          <div class="form-grid">
            <div class="field">
              <label for="photo-file">사진 파일</label>
              <input id="photo-file" type="file" accept="image/*" @change="onFileChange">
            </div>
            <div class="field">
              <label for="photo-comment">메모</label>
              <textarea id="photo-comment" v-model="uploadForm.comment" placeholder="이 장면에서 기억하고 싶은 내용을 남겨 보세요." />
            </div>
            <div class="field">
              <label for="photo-location">장소</label>
              <input id="photo-location" v-model="uploadForm.location" placeholder="예: Gion, Busan, Paris Left Bank">
            </div>
            <div class="actions">
              <button class="button" type="button" :disabled="uploadPending" @click="upload">
                {{ uploadPending ? 'Uploading...' : '사진 추가' }}
              </button>
            </div>
          </div>
        </div>
      </section>

      <section class="panel">
        <div class="split-header">
          <div>
            <span class="eyebrow">Photo Sequence</span>
            <h2 class="section-title">사진 시퀀스</h2>
          </div>
        </div>

        <div v-if="photos?.length" class="content-grid">
          <article v-for="photo in photos" :key="photo.id" class="photo-card">
            <img :src="photo.imageUrl" :alt="photo.comment || travel.title">
            <div class="photo-card__body">
              <div class="meta-row">
                <span>#{{ photo.sortOrder }}</span>
                <span>{{ photo.takenDate || 'Date not set' }}</span>
              </div>

              <div class="form-grid" style="margin-top: 14px;">
                <div class="field">
                  <label>메모</label>
                  <textarea v-model="photo.comment" />
                </div>
                <div class="field">
                  <label>장소</label>
                  <input v-model="photo.location">
                </div>
                <div class="field">
                  <label>촬영일</label>
                  <input v-model="photo.takenDate" type="date">
                </div>
              </div>

              <div class="photo-card__actions">
                <button class="ghost-button" type="button" @click="movePhoto(photo.id, -1)">위로</button>
                <button class="ghost-button" type="button" @click="movePhoto(photo.id, 1)">아래로</button>
                <button class="button" type="button" :disabled="savePendingId === photo.id" @click="savePhoto(photo)">
                  {{ savePendingId === photo.id ? 'Saving...' : '저장' }}
                </button>
                <button class="danger-button" type="button" @click="removePhoto(photo.id)">삭제</button>
              </div>
            </div>
          </article>
        </div>

        <EmptyState
          v-else
          title="아직 업로드한 사진이 없습니다"
          description="상단 업로드 영역에서 첫 장면을 추가해 보세요."
        />
      </section>
    </div>

    <aside style="display: grid; gap: 24px;">
      <section class="panel">
        <span class="eyebrow">Project Snapshot</span>
        <div class="stat-grid">
          <div class="stat">
            <strong>{{ photos?.length || 0 }}</strong>
            <span class="muted">컬렉션 사진</span>
          </div>
          <div class="stat">
            <strong>{{ travel.bookUid ? 'Yes' : 'No' }}</strong>
            <span class="muted">SweetBook 연결 여부</span>
          </div>
        </div>
      </section>

      <section class="panel">
        <span class="eyebrow">Next Step</span>
        <h3 style="font-size: 1.4rem; margin: 14px 0 10px;">템플릿을 고르고 책으로 묶기</h3>
        <p class="muted">
          사진이 어느 정도 쌓였다면 프리뷰 화면에서 판형과 테마를 선택한 뒤 실제 출력용 책을 빌드할 수 있습니다.
        </p>
        <div class="actions">
          <NuxtLink class="button" :to="`/travels/${travel.id}/preview`">프리뷰 열기</NuxtLink>
        </div>
      </section>
    </aside>
  </div>
</template>
