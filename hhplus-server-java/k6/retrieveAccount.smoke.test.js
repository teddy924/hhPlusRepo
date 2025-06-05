import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 1, // 가상 사용자 수
    duration: '30s', // 테스트 시간
};

export default function () {
    // 실제 테스트 대상 서버 주소 (애플리케이션 컨테이너 또는 로컬)
    const res = http.get('http://host.docker.internal:8080/api/account/balance?userId=1');

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

}
