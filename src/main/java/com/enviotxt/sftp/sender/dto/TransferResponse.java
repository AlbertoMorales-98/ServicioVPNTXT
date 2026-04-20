package com.enviotxt.sftp.sender.dto;

public record TransferResponse(
        String localPath,
        String remotePath,
        String remoteMessage
) {
}
