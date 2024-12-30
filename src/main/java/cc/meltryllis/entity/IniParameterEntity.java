package cc.meltryllis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class IniParameterEntity {
    private String section;
    private String key;
    private String value;
}
