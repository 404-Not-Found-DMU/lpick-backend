package com.notfound.lpickbackend.wiki.command.application.doc;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "wiki_pages") // 인덱스명
@Setting(settingPath = "/es/wiki_page-settings.json")
@Mapping(mappingPath = "/es/wiki_page-mappings.json")
public class WikiPageDoc {
}
