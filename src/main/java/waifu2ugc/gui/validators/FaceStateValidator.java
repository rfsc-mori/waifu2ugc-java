package waifu2ugc.gui.validators;

import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.FaceIndex;

import javax.swing.JFrame;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class FaceStateValidator extends TemplateStateValidator
{
	protected List<TemplateFace> badFaces;

	protected FaceStateValidator(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
		this.template = template;
	}

	protected Stream<TemplateFace> getBadFaces() {
		return badFaces.stream();
	}

	protected int getBadFacesCount() {
		return badFaces.size();
	}

	protected String getBadFacesAsString() {
		return badFaces.stream()
		               .map(TemplateFace::getIndex)
		               .map(FaceIndex::getAlias)
		               .collect(Collectors.joining(", "));
	}

	@Override
	public boolean validate() {
		return updateBadFaces() && badFaces.isEmpty();
	}

	protected boolean updateBadFaces() {
		badFaces = template.getEnabledFaces()
		                   .filter(face -> !validateTemplateFace(face))
		                   .collect(Collectors.toList());
		return true;
	}

	abstract protected boolean validateTemplateFace(TemplateFace face);

	@Override
	protected String getMessage() {
		return String.format(getMessageFormat(), getBadFacesAsString());
	}

	abstract protected String getMessageFormat();
}
