package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Metadata;

public class MetadataBuilder {

	private Metadata metadata = new Metadata();
	
	public static MetadataBuilder metadata() {
		return new MetadataBuilder();
	}
	
	public MetadataBuilder with(LanguageBuilder language) {
		metadata.getLanguages().add(language.build());
		return this;
	}
	
	public MetadataBuilder withSoftwareName(String name) {
		metadata.setOcrSoftwareName(name);
		return this;
	}

	public MetadataBuilder withSoftwareVersion(String version) {
		metadata.setOcrSoftwareVersion(version);
		return this;
	}

	public Metadata build() {
		return metadata;
	}
	
}
