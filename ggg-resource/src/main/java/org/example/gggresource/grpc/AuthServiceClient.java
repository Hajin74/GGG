package org.example.gggresource.grpc;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gggresource.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.example.ggg.grpc.AuthServiceGrpc;
import org.example.ggg.grpc.AuthRequest;
import org.example.ggg.grpc.AuthResponse;

@Slf4j
@Service
public class AuthServiceClient {

    @GrpcClient("auth")
    private AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;

    public UserResponse authenticateUser(String accessToken) {
        try {
            AuthResponse response = authServiceBlockingStub.authenticateUser(
                    AuthRequest.newBuilder()
                            .setAccessToken(accessToken)
                            .build());

            return new UserResponse(response.getSuccess(), response.getId(), response.getUsername());
        } catch (StatusRuntimeException exception) {
            log.info("gRPC 호출 실패 : {}", exception.getStatus().getCode().name());
            return new UserResponse(false, -1, "Unknown");
        }
    }

}
