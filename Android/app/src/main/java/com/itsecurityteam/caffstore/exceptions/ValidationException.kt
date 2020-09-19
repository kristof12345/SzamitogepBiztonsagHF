package com.itsecurityteam.caffstore.exceptions

class ValidationException(val stringCode: Int) : RuntimeException("Validation exception") {}