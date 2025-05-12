package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.common.DistributedLockExecutor;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.LockAcquireFailException;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
public class ProductLockService {

    private final DistributedLockExecutor lockExecutor;
    private final ProductService productService;

    public ProductLockService(DistributedLockExecutor lockExecutor, ProductService productService) {
        this.lockExecutor = lockExecutor;
        this.productService = productService;
    }

    public void restoreStockLock(Long productId, int quantity) {
        int retryCount = 5;
        String lockKey = "lock:product:" + productId;

        while (retryCount-- > 0) {
            try {
                lockExecutor.runWithLock(lockKey, () -> {
                    productService.restoreStock(productId, quantity);
                });
                return; // 락 획득 및 실행 성공 시 종료
            } catch (LockAcquireFailException e1) {
                // 락 획득 실패 → 재시도
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();  // 인터럽트 상태 복원
                    throw new RuntimeException("Retry sleep interrupted while restoring stock", e2);
                }
            }
        }

        // 재시도 실패 시 예외 발생
        throw new CustomException(FAIL_RESTORE_STOCK);
    }

    public void decreaseStockLock(Long productId, int quantity) {
        int retryCount = 5;
        String lockKey = "lock:product:" + productId;

        while (retryCount-- > 0) {
            try {
                lockExecutor.runWithLock(lockKey, () -> {
                    productService.decreaseStock(productId, quantity);
                });
                return; // 락 획득 및 실행 성공 시 종료
            } catch (LockAcquireFailException e1) {
                // 락 획득 실패 → 재시도
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();  // 인터럽트 상태 복원
                    throw new RuntimeException("Retry sleep interrupted while restoring stock", e2);
                }
            }
        }

        // 재시도 실패 시 예외 발생
        throw new CustomException(FAIL_DECREASE_STOCK);

    }
}
