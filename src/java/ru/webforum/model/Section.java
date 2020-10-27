package ru.webforum.model;

import java.util.Set;

public class Section implements Cloneable
{
    private Integer sectionId;
    private String name;
	private String description;

    private Section upsection = null;
    private Set<Section> downsections = null;
    private Set<Topic> topics = null;
    
    public Section() {}

	public Section(Integer sectionId, String name, String description) 
    {
		this.sectionId = sectionId;
		this.name = name;
		this.description = description;
	}

    public Integer getSectionId() { return this.sectionId; }
    public void setSectionId(Integer sectionId) { this.sectionId = sectionId; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }


    public Section getUpsection() { return this.upsection; }
    public void setUpsection(Section upsection) { this.upsection = upsection; }

    public Set<Section> getDownsections() { return this.downsections; }
    public void setDownsections(Set<Section> downsections) { this.downsections = downsections; }

    public Set<Topic> getTopics() { return this.topics; }
    public void setTopics(Set<Topic> topics) { this.topics = topics; }

    public Section clone() throws CloneNotSupportedException
    {
        return (Section) super.clone();
    }
}