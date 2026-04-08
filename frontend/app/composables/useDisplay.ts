export function useDisplay() {
  function formatStatus(status?: string | null) {
    switch (status) {
      case 'DRAFT':
        return '작성중'
      case 'COMPLETED':
        return '제작완료'
      case 'ORDERED':
        return '주문완료'
      case 'CREATED':
        return '생성완료'
      case 'CANCELED':
        return '취소됨'
      case 'PENDING':
        return '대기중'
      default:
        return status || '-'
    }
  }

  return {
    formatStatus
  }
}
