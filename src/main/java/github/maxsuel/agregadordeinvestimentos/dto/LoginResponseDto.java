package github.maxsuel.agregadordeinvestimentos.dto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after a successful login")
public record LoginResponseDto(

    @Schema(
        description = "The access token for authenticated requests",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    String accessToken

) {
}