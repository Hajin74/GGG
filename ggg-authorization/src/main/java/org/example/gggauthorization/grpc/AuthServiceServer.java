package org.example.gggauthorization.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.ggg.grpc.AuthServiceGrpc;
import org.example.ggg.grpc.AuthRequest;
import org.example.ggg.grpc.AuthResponse;

@GrpcService
public class AuthServiceServer extends AuthServiceGrpc.AuthServiceImplBase {

    @Override
    public void authenticateUser(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        // 응답 생성
        AuthResponse response = AuthResponse.newBuilder()
                .setSuccess(true)
                .setUsername(request.getAccessToken())
                .setPassword(request.getAccessToken())
                .build();

        // 클라이언트로 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
