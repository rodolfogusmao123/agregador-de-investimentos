package github.maxsuel.agregadordeinvestimentos.controller;

import github.maxsuel.agregadordeinvestimentos.dto.request.stock.CreateStockDto;
import github.maxsuel.agregadordeinvestimentos.dto.response.stock.UserStockSummaryDto;
import github.maxsuel.agregadordeinvestimentos.entity.User;
import github.maxsuel.agregadordeinvestimentos.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@Tag(name = "Stocks", description = "Managing the catalog of assets available in the system.")
public class StockController {

    private final StockService stockService;

    @Operation(
            summary = "Register a new stock.",
            description = "Adds a ticker (ex: PETR4) to the system's database."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Stock successfully registered."
    )
    @PostMapping
    public ResponseEntity<Void> createStock(@Valid @RequestBody CreateStockDto createStockDto) {
        stockService.createStock(createStockDto);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "List owned stocks",
            description = "Returns a consolidated list of all stocks owned by the authenticated user across all their accounts.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<UserStockSummaryDto>> getOwnedStocks(@AuthenticationPrincipal User user) {
        var ownedStocks = stockService.listOwnedStocks(user);

        return ResponseEntity.ok(ownedStocks);
    }

}
