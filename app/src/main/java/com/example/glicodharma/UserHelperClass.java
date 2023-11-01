package com.example.glicodharma;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserHelperClass {

    String nome, username, email, telefone, senha;

    public UserHelperClass() {
    }

    public UserHelperClass(String nome, String username, String email, String telefone, String senha) {
        this.nome = nome;
        this.username = username;
        this.email = email;
        this.telefone = telefone;
        this.senha = criptografarSenha(senha);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    private String criptografarSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(senha.getBytes());

            // Converter o array de bytes em uma representação hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
