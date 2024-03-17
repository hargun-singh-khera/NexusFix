package com.example.laptoprepairapp

data class RequestModel (
    var ticketId: String?= null,
    var laptopModel: String?= null,
    var problemDesc: String?= null,
    var remarks: String ?= null,
    var reqCompleted: Boolean ?= false
)