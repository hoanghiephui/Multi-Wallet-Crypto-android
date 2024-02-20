package io.horizontalsystems.bankwallet.endpoint

import java.io.IOException

class NoNetworkException(cause: Exception) : IOException(cause)
