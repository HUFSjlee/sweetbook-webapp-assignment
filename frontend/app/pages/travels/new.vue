<script setup lang="ts">
const api = useApi()
const router = useRouter()

const form = reactive({
  title: '',
  description: '',
  startDate: '',
  endDate: '',
  mood: ''
})

const submitting = ref(false)
const errorMessage = ref('')

async function submit() {
  try {
    submitting.value = true
    errorMessage.value = ''

    const travel = await api.createTravel({ ...form })
    await router.push(`/travels/${travel.id}`)
  } catch (error) {
    errorMessage.value = '프로젝트 생성에 실패했습니다.'
    console.error(error)
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="two-column">
    <div class="glass-panel">
      <span class="eyebrow">Start A New Journey</span>
      <h1 class="section-title">새 여행 프로젝트를 엽니다</h1>
      <p class="muted">
        제목, 기간, 분위기만 먼저 정하면 됩니다. 이후 사진을 쌓고, 코멘트를 더하고, SweetBook 테마를 고르는 흐름으로 이어집니다.
      </p>

      <div class="timeline" style="margin-top: 24px;">
        <div class="timeline__item">
          <strong>1. 여행 생성</strong>
          <span class="muted">프로젝트 이름과 기간을 저장합니다.</span>
        </div>
        <div class="timeline__item">
          <strong>2. 사진 업로드</strong>
          <span class="muted">사진마다 짧은 메모와 장소를 정리합니다.</span>
        </div>
        <div class="timeline__item">
          <strong>3. 포토북 제작</strong>
          <span class="muted">판형과 테마를 선택한 뒤 출력용 책을 빌드합니다.</span>
        </div>
      </div>
    </div>

    <div class="panel">
      <div v-if="errorMessage" class="error-box">
        {{ errorMessage }}
      </div>

      <form class="form-grid" @submit.prevent="submit">
        <div class="field">
          <label for="title">여행 제목</label>
          <input id="title" v-model="form.title" placeholder="예: Kyoto Golden Hour" required>
        </div>

        <div class="field">
          <label for="description">한 줄 소개</label>
          <textarea
            id="description"
            v-model="form.description"
            placeholder="이번 여행에서 남기고 싶은 감정이나 장면을 짧게 적어보세요."
          />
        </div>

        <div class="form-grid two">
          <div class="field">
            <label for="startDate">시작일</label>
            <input id="startDate" v-model="form.startDate" type="date">
          </div>

          <div class="field">
            <label for="endDate">종료일</label>
            <input id="endDate" v-model="form.endDate" type="date">
          </div>
        </div>

        <div class="field">
          <label for="mood">분위기</label>
          <input id="mood" v-model="form.mood" placeholder="Warm, Coastal, Quiet, Night Train...">
        </div>

        <div class="actions">
          <button class="button" type="submit" :disabled="submitting">
            {{ submitting ? 'Creating...' : '프로젝트 만들기' }}
          </button>
          <NuxtLink class="ghost-button" to="/travels">목록으로</NuxtLink>
        </div>
      </form>
    </div>
  </section>
</template>
