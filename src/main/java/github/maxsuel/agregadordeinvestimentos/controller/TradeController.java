package github.maxsuel.agregadordeinvestimentos.controller;

import github.maxsuel.agregadordeinvestimentos.dto.request.stock.TradeRequestDto;
import github.maxsuel.agregadordeinvestimentos.dto.response.stock.PortfolioResponseDto;
import github.maxsuel.agregadordeinvestimentos.dto.response.stock.TransactionsResponseDto;
import github.maxsuel.agregadordeinvestimentos.entity.User;
import github.maxsuel.agregadordeinvestimentos.exceptions.dto.ErrorResponseDto;
import github.maxsuel.agregadordeinvestimentos.mapper.TransactionMapper;
import github.maxsuel.agregadordeinvestimentos.repository.TransactionsRepository;
import github.maxsuel.agregadordeinvestimentos.service.AccountService;
import github.maxsuel.agregadordeinvestimentos.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
@Tag(
    name = "Trades",
    description = "Endpoints for asset trading and transaction history"
)
public class TradeController {

    private final TradeService tradeService;
    private final TransactionsRepository transactionsRepository;
    private final TransactionMapper transactionMapper;
    private final AccountService accountService;

    @Operation(
            summary = "Execute a buy order",
            description = "Validates user cash, fetches real-time price, updates average price, and logs the transaction.",
            operationId = "buyStock"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase executed successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient funds or invalid parameters",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User, Account or Stock not found",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/buy")
    public ResponseEntity<Void> buy(@RequestBody @Valid TradeRequestDto dto,
                                    @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        tradeService.executeBuy(user, dto);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Execute a sell order",
            description = "Verifies shares availability, credits cash to user, and logs the transaction.",
            operationId = "sellStock"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sale successful"),
            @ApiResponse(responseCode = "400", description = "Insufficient shares for the requested operation",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Account or Asset not found",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/sell")
    public ResponseEntity<Void> sell(@RequestBody @Valid TradeRequestDto dto,
                                     @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        tradeService.executeSell(user, dto);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get transaction history",
            description = "Returns a list of all buy and sell operations performed by the authenticated user.",
            operationId = "getTransactionHistory"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions retrieved",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionsResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/history")
    public ResponseEntity<List<TransactionsResponseDto>> getHistory(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                transactionsRepository.findAllByUser_UserId(user.getUserId())
                        .stream()
                        .map(transactionMapper::toDto)
                        .toList()
        );
    }

    @Operation(
            summary = "Get consolidated portfolio",
            description = "Calculates total equity by summing available user cash and real-time stock market values for a specific account.",
            operationId = "getAccountPortfolio"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Portfolio calculated successfully",
                    content = @Content(schema = @Schema(implementation = PortfolioResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found or does not belong to the user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/portfolio/{accountId}")
    public ResponseEntity<PortfolioResponseDto> getPortfolio(@PathVariable String accountId,
                                                             @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(accountService.getCompletePortfolio(user, accountId));
    }

}
