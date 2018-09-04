package io.github.rowak;

/**
 * A base exception to parent the implementations.
 */
public class StatusCodeException extends Exception
{
	private static final long serialVersionUID = -5715052565433374434L;

	public StatusCodeException() {}
	
	public StatusCodeException(String message)
	{
		super(message);
	}
	
	/**
	 * Thrown whenever the Aurora controlller returns a 400 status code response.
	 */
	public class BadRequestException extends StatusCodeException
	{
		private static final long serialVersionUID = 4005174839720417274L;

		public BadRequestException()
		{
			super("400 Bad Request.");
		}
	}
	
	/**
	 * Thrown whenever the Aurora controller returns a 401 status code response.
	 * Always thrown whenever the OpenAPI is called with an invalid access token.
	 */
	public class UnauthorizedException extends StatusCodeException
	{
		private static final long serialVersionUID = 7130863936845580203L;

		public UnauthorizedException()
		{
			super("401 Unauthorized.");
		}
	}
	
	/**
	 * Thrown whenever the Aurora controlller returns a 403 status code response.
	 */
	public class ForbiddenException extends StatusCodeException
	{
		private static final long serialVersionUID = -7283067396965188L;

		public ForbiddenException()
		{
			super("403 Forbidden.");
		}
	}
	
	/**
	 * Thrown whenever the Aurora controlller returns a 404 status code response.
	 * Always thrown whenever a resource is attempted to be accessed or modified
	 * on the Aurora when it does not exist.
	 */
	public class ResourceNotFoundException extends StatusCodeException
	{
		private static final long serialVersionUID = 530981082371068310L;

		public ResourceNotFoundException()
		{
			super("404 Resource Not Found.");
		}
	}
	
	/**
	 * Thrown whenever the Aurora controlller returns a 422 status code response.
	 * Always thrown whenever a request is sent with a body that is malformed in any way.
	 */
	public class UnprocessableEntityException extends StatusCodeException
	{
		private static final long serialVersionUID = -6690847964007459719L;

		public UnprocessableEntityException()
		{
			super("422 Unprocessable Entity.");
		}
	}
	
	/**
	 * Thrown whenever the Aurora controlller returns a 500 status code response.
	 */
	public class InternalServerErrorException extends StatusCodeException
	{
		private static final long serialVersionUID = -2133759736781247975L;

		public InternalServerErrorException()
		{
			super("500 Internal Server Error.");
		}
	}
}
