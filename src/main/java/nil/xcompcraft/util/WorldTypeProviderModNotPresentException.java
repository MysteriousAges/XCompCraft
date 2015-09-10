package nil.xcompcraft.util;

public class WorldTypeProviderModNotPresentException extends RuntimeException {

	private static final long serialVersionUID = 558761386738091888L;

	public WorldTypeProviderModNotPresentException(String message) {
		super(message);
	}
	
	public WorldTypeProviderModNotPresentException(String message, Throwable cause) {
		super(message, cause);
	}
}
