package com.marcelo.chatBot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest{

    @NotBlank
    private String mensagem;

    @NotBlank
    private String idUsuario;

    
    @NotBlank
    private String conversationId;
}