package com.odde.doughnut.services.wikidataApis.thirdPartyEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.odde.doughnut.services.wikidataApis.WikidataValue;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WikidataEntity {
  @Getter @Setter protected Map<String, List<WikidataClaimItem>> claims;

  public Optional<WikidataValue> getFirstClaimValue(String propertyId) {
    if (claims == null) {
      return Optional.empty();
    }
    List<WikidataClaimItem> listOfItems = claims.getOrDefault(propertyId, null);
    if (listOfItems == null || listOfItems.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(listOfItems.get(0).toWikidataValue());
  }
}
