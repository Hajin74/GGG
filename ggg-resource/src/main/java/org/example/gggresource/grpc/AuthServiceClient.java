package org.example.gggresource.grpc;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.example.ggg.grpc.AuthServiceGrpc;
import org.example.ggg.grpc.AuthRequest;
import org.example.ggg.grpc.AuthResponse;

@Slf4j
@Service
public class AuthServiceClient {

    @GrpcClient("auth")
    private AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;

    public String authenticateUser(String accessToken) {
        try {
            AuthResponse response = authServiceBlockingStub.authenticateUser(
                    AuthRequest.newBuilder()
                            .setAccessToken(accessToken)
                            .build());

            return response.toString();
        } catch (StatusRuntimeException exception) {
            return "[FAIL] " + exception.getStatus().getCode().name();
        }
    }

}
