package org.onecx.app.document.management.bff.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.onecx.app.document.management.bff.exception.RestException;

import gen.org.tkit.onecx.document_management.rs.internal.model.ProblemDetailResponseDTO;

@Mapper
public interface ExceptionMapper {

    @Mapping(target = "detail", source = "message")
    @Mapping(target = "params", ignore = true)
    @Mapping(target = "invalidParams", ignore = true)
    @Mapping(target = "removeParamsItem", ignore = true)
    @Mapping(target = "removeInvalidParamsItem", ignore = true)
    ProblemDetailResponseDTO map(RestException restException);
}
