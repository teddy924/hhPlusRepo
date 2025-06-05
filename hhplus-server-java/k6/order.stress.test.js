import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '2m',
    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<1000'],
    },
};

// 사용자 ID 100~199
const userIds = Array.from({ length: 100 }, (_, i) => 500 + i);

export default function () {
    const index = (__VU * 1000 + __ITER) % userIds.length;
    const userId = userIds[index];
    const productId = 900011;

    const payload = JSON.stringify({
        userId: userId,
        productGrp: {
            [productId]: 1, // Map<Long, Integer> 형태
        },
        couponId: null, // or 테스트용 쿠폰 ID
        orderAddressDTO: {
            receiverName: "부하테스트_" + userId,
            phone: "010-1111-1" + userId,
            address1: "서울시 드디어구 끝이동 " + userId,
            address2: userId + "동 " + userId + "호",
            zipcode: "12345",
            memo: "memo당 " + userId,
            roadNameAddress: "서울시 강남구 테헤란로 1",
            detailAddress: "101호",
        }
    });

    const res = http.post('http://host.docker.internal:8080/api/order/order', payload, {
        headers: { 'Content-Type': 'application/json' },
    });

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response is not empty': (r) => r.body && r.body.length > 0,
    });

    sleep(1);
}
