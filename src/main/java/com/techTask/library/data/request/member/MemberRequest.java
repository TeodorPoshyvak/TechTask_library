package com.techTask.library.data.request.member;

public class MemberRequest {
    String name;

    public MemberRequest() {
    }

    public MemberRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
