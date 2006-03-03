package freenet.node.fcp;

import java.net.MalformedURLException;

import freenet.client.FailureCodeTracker;
import freenet.client.InserterException;
import freenet.keys.FreenetURI;
import freenet.node.Node;
import freenet.support.Fields;
import freenet.support.SimpleFieldSet;

public class PutFailedMessage extends FCPMessage {

	final int code;
	final String codeDescription;
	final String extraDescription;
	final String shortCodeDescription;
	final FailureCodeTracker tracker;
	final FreenetURI expectedURI;
	final String identifier;
	final boolean isFatal;
	
	public PutFailedMessage(InserterException e, String identifier) {
		this.code = e.getMode();
		this.codeDescription = InserterException.getMessage(code);
		this.shortCodeDescription = InserterException.getShortMessage(code);
		this.extraDescription = e.extra;
		this.tracker = e.errorCodes;
		this.expectedURI = e.uri;
		this.identifier = identifier;
		this.isFatal = InserterException.isFatal(code);
	}

	/**
	 * Construct from a fieldset. Used in serialization of persistent requests.
	 * Will need to be made more tolerant of syntax errors if is used in an FCP
	 * client library. FIXME.
	 * @param useVerboseFields If true, read in verbose fields (CodeDescription
	 * etc), if false, reconstruct them from the error code.
	 * @throws MalformedURLException 
	 */
	public PutFailedMessage(SimpleFieldSet fs, boolean useVerboseFields) throws MalformedURLException {
		identifier = fs.get("Identifier");
		if(identifier == null) throw new NullPointerException();
		code = Integer.parseInt(fs.get("Code"));
		
		if(useVerboseFields) {
			codeDescription = fs.get("CodeDescription");
			isFatal = Fields.stringToBool(fs.get("Fatal"), false);
			shortCodeDescription = fs.get("ShortCodeDescription");
		} else {
			codeDescription = InserterException.getMessage(code);
			isFatal = InserterException.isFatal(code);
			shortCodeDescription = InserterException.getShortMessage(code);
		}
		
		extraDescription = fs.get("ExtraDescription");
		String euri = fs.get("ExpectedURI");
		expectedURI = new FreenetURI(euri);
		SimpleFieldSet trackerSubset = fs.subset("Errors");
		if(trackerSubset != null) {
			tracker = new FailureCodeTracker(true, trackerSubset);
		} else {
			tracker = null;
		}
	}

	public SimpleFieldSet getFieldSet() {
		return getFieldSet(true);
	}
	
	public SimpleFieldSet getFieldSet(boolean verbose) {
		SimpleFieldSet fs = new SimpleFieldSet(false);
		fs.put("Identifier", identifier);
		fs.put("Code", Integer.toString(code));
		if(verbose)
			fs.put("CodeDescription", codeDescription);
		if(extraDescription != null)
			fs.put("ExtraDescription", extraDescription);
		if(tracker != null) {
			tracker.copyToFieldSet(fs, "Errors.", verbose);
		}
		if(verbose)
			fs.put("Fatal", Boolean.toString(isFatal));
		if(verbose)
			fs.put("ShortCodeDescription", shortCodeDescription);
		if(expectedURI != null)
			fs.put("ExpectedURI", expectedURI.toString());
		return fs;
	}

	public String getName() {
		return "PutFailed";
	}

	public void run(FCPConnectionHandler handler, Node node)
			throws MessageInvalidException {
		throw new MessageInvalidException(ProtocolErrorMessage.INVALID_MESSAGE, "PutFailed goes from server to client not the other way around", identifier);
	}

}
