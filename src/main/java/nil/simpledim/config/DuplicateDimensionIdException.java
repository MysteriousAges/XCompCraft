package nil.simpledim.config;

public class DuplicateDimensionIdException extends RuntimeException {
	private static final long serialVersionUID = 5054027003603485015L;
	
	public DuplicateDimensionIdException(String message) {
		super(message);
	}
}
