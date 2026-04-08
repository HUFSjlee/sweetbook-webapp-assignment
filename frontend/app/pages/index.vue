<script setup lang="ts">
import type { Travel } from '~/app/types/api'

const api = useApi()
const { data: travels, error } = await useAsyncData('home-travels', () => api.getTravels())

const featuredTravels = computed<Travel[]>(() => (travels.value || []).slice(0, 3))
</script>

<template>
  <div style="display: grid; gap: 28px;">
    <section class="hero-grid">
      <div class="glass-panel">
        <span class="eyebrow">Printed Memory Engine</span>
        <h1 class="headline">
          여행의 리듬을
          <br>
          한 권의 포토북으로.
        </h1>
        <p class="lede">
          사진 업로드, 짧은 메모, 템플릿 선택까지 한 흐름으로 묶었습니다.
          TravelogBook은 SweetBook 출력 파이프라인까지 연결된 여행 포토북 작업실입니다.
        </p>

        <div class="actions">
          <NuxtLink class="button" to="/travels/new">새 여행 시작</NuxtLink>
          <NuxtLink class="ghost-button" to="/travels">샘플 프로젝트 보기</NuxtLink>
        </div>

        <div class="stat-grid">
          <div class="stat">
            <strong>1</strong>
            <span class="muted">사진과 코멘트를 한곳에 정리</span>
          </div>
          <div class="stat">
            <strong>2</strong>
            <span class="muted">SweetBook 템플릿으로 실제 제작</span>
          </div>
          <div class="stat">
            <strong>3</strong>
            <span class="muted">견적 확인 후 주문까지 연결</span>
          </div>
        </div>
      </div>

      <div class="preview-stack">
        <div class="preview-book">
          <div class="preview-book__cover">
            <div class="preview-book__meta">
              <span class="eyebrow">Live Workflow</span>
              <strong>Upload → Curate → Print</strong>
              <p class="muted">
                여행 프로젝트를 만들고, 사진 순서를 다듬고, 좋아하는 북 테마를 선택한 뒤 바로 제작 단계로 넘깁니다.
              </p>
            </div>
            <img
              src="https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80"
              alt="Travel preview"
            >
          </div>
        </div>

        <div class="panel">
          <div class="split-header">
            <div>
              <span class="eyebrow">Service Focus</span>
              <h2 class="section-title">왜 이 구조가 빠른가</h2>
            </div>
          </div>
          <div class="timeline">
            <div class="timeline__item">
              <strong>프로젝트 중심</strong>
              <span class="muted">여행 단위로 사진, 메모, 주문 흐름을 관리합니다.</span>
            </div>
            <div class="timeline__item">
              <strong>실제 출력 연결</strong>
              <span class="muted">백엔드가 SweetBook API를 대리 호출해 인쇄 흐름을 보장합니다.</span>
            </div>
            <div class="timeline__item">
              <strong>시각적 작업 환경</strong>
              <span class="muted">사진 중심 레이아웃으로 모바일과 데스크톱 모두에서 바로 검토할 수 있습니다.</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="panel">
      <div class="split-header">
        <div>
          <span class="eyebrow">Current Projects</span>
          <h2 class="section-title">바로 열어볼 수 있는 여행들</h2>
        </div>
        <NuxtLink class="ghost-button" to="/travels">전체 보기</NuxtLink>
      </div>

      <div v-if="error" class="error-box">
        여행 목록을 불러오지 못했습니다. 백엔드 실행 여부를 확인해 주세요.
      </div>

      <div v-else-if="featuredTravels.length" class="content-grid">
        <TravelCard v-for="travel in featuredTravels" :key="travel.id" :travel="travel" />
      </div>

      <EmptyState
        v-else
        title="아직 생성된 여행이 없습니다"
        description="새 프로젝트를 만들면 사진 업로드부터 포토북 프리뷰, 주문 흐름까지 바로 이어집니다."
        action-label="첫 여행 만들기"
        action-to="/travels/new"
      />
    </section>
  </div>
</template>
