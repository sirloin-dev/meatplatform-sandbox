package test.large.endpoint.v1.user

import com.sirloin.sandbox.server.api.endpoint.v1.user.request.CreateUserRequest
import com.sirloin.sandbox.server.api.endpoint.v1.user.request.UpdateUserRequest
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.DeletedUserResponse
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.UserResponse
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import test.large.endpoint.v1.asPrefixedRequestField
import test.large.endpoint.v1.asRequestField
import test.large.endpoint.v1.basicResponseFieldsSnippet

fun createUserRequestFieldsSnippet(): List<FieldDescriptor> = listOf(
    fieldWithPath(CreateUserRequest::nickname.asRequestField())
        .type(JsonFieldType.STRING)
        .description(CreateUserRequest.DESC_NICKNAME),
    fieldWithPath(CreateUserRequest::profileImageUrl.asRequestField())
        .type(JsonFieldType.STRING)
        .description(CreateUserRequest.DESC_PROFILE_IMAGE_URL)
)

fun updateUserRequestFieldsSnippet(): List<FieldDescriptor> = listOf(
    fieldWithPath(UpdateUserRequest::nickname.asRequestField())
        .type(JsonFieldType.STRING)
        .description(UpdateUserRequest.DESC_NICKNAME)
        .optional(),
    fieldWithPath(UpdateUserRequest::profileImageUrl.asRequestField())
        .type(JsonFieldType.STRING)
        .description(UpdateUserRequest.DESC_PROFILE_IMAGE_URL)
        .optional()
)

fun userInfoResponseFieldsSnippet(): List<FieldDescriptor> = ArrayList(basicResponseFieldsSnippet()) + listOf(
    fieldWithPath(UserResponse::uuid.asPrefixedRequestField("body"))
        .type(JsonFieldType.STRING)
        .description(UserResponse.DESC_UUID),
    fieldWithPath(UserResponse::nickname.asPrefixedRequestField("body"))
        .type(JsonFieldType.STRING)
        .description(UserResponse.DESC_NICKNAME),
    fieldWithPath(UserResponse::profileImageUrl.asPrefixedRequestField("body"))
        .type(JsonFieldType.STRING)
        .description(UserResponse.DESC_PROFILE_IMAGE_URL),
)

fun deletedUserInfoResponseFieldsSnippet(): List<FieldDescriptor> = ArrayList(basicResponseFieldsSnippet()) + listOf(
    fieldWithPath(DeletedUserResponse::uuid.asPrefixedRequestField("body"))
        .type(JsonFieldType.STRING)
        .description(DeletedUserResponse.DESC_UUID)
)
