package com.example.laptoprepairapp

data class RequestModel (
    var userId: String?= null,
    var ticketId: String?= null,
    var laptopModel: String?= null,
    var problemDesc: String?= null
)