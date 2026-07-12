package hwr.oop.examples.template.service

import hwr.oop.examples.template.ports.out.LoadGameByIdPort
import hwr.oop.examples.template.service.model.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerAdvice {

	@ExceptionHandler(LoadGameByIdPort.CouldNotLoadException::class)
	fun handleGameNotFound(ex: Exception): ResponseEntity<ErrorResponse> {
		return errorResponse(HttpStatus.NOT_FOUND, ex)
	}

	@ExceptionHandler(IllegalStateException::class)
	fun handleGameStateError(ex: Exception): ResponseEntity<ErrorResponse> {
		return errorResponse(HttpStatus.CONFLICT, ex)
	}

	@ExceptionHandler(
		IllegalArgumentException::class,
		HttpMessageNotReadableException::class,
		MethodArgumentNotValidException::class,
	)
	fun handleBadRequest(ex: Exception): ResponseEntity<ErrorResponse> {
		return errorResponse(HttpStatus.BAD_REQUEST, ex)
	}

	@ExceptionHandler(Exception::class)
	fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
		return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex)
	}

	private fun errorResponse(status: HttpStatus, ex: Exception): ResponseEntity<ErrorResponse> {
		val errorResponse = ErrorResponse(
			/*status =*/ status.value(),
			/*error =*/ status.reasonPhrase,
			/*message =*/ ex.message ?: "An unexpected error occurred",
		)
		return ResponseEntity.status(status)
			.body(errorResponse)
	}
}

