package com.application.library.converter;

import com.application.library.data.dto.BaseUserSaveRequestDto;
import com.application.library.data.dto.UserSaveRequestDto;
import com.application.library.model.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {}
)
public abstract class UserConverter {


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mapping(source = "dto.password", target = "password", qualifiedByName = "encodePassword")
    public abstract User toEntity(BaseUserSaveRequestDto dto);

    @Mapping(source = "dto.password", target = "password", qualifiedByName = "encodePassword")
    public abstract User toEntity(UserSaveRequestDto dto);


    @Named("encodePassword")
    String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

}
