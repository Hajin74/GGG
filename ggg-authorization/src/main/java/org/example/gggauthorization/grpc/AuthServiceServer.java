package org.example.gggauthorization.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.ggg.grpc.AuthServiceGrpc;
import org.example.ggg.grpc.AuthRequest;
import org.example.ggg.grpc.AuthResponse;
import org.example.gggauthorization.auth.JwtService;
import org.example.gggauthorization.domain.entity.User;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AuthServiceServer extends AuthServiceGrpc.AuthServiceImplBase {

    private final JwtService jwtService;

    @Override
    public void authenticateUser(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        log.info("[jwtService] authenticateUser - token : {}", request.getAccessToken());

        // 토큰 검증 및 사용자 정보 획득
        User user = jwtService.validateAccessToken(request.getAccessToken());

        // 응답 생성
        AuthResponse response = AuthResponse.newBuilder()
                .setSuccess(true)
                .setId(user.getId())
                .setUsername(user.getUsername())
                .build();

        // 클라이언트로 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
