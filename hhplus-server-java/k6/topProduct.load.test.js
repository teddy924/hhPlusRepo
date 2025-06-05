import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 50,              // 가상 사용자 수
    duration: '2m',       // 테스트 시간
    thresholds: {
        http_req_failed: ['rate<0.01'],  // 실패율 < 1%
        http_req_duration: ['p(95)<300'], // 95% 요청은 300ms 이내
    },
};

export default function () {
    const categories = ['TENT','ALL'];
    const category = categories[Math.floor(Math.random() * categories.length)];

    const res = http.get(`http://host.docker.internal:8080/api/product/top?category=${category}`);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response is not empty': (r) => r.body && r.body.length > 20,
    });

    sleep(1); // 사용자 간 간격
}
