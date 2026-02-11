package github.maxsuel.agregadordeinvestimentos.mapper;

import github.maxsuel.agregadordeinvestimentos.dto.response.stock.TransactionsResponseDto;
import github.maxsuel.agregadordeinvestimentos.entity.Transactions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "stockId", source = "stock.stockId")
    @Mapping(target = "totalValue", expression = "java(transactions.getPriceAtTime().multiply(java.math.BigDecimal.valueOf(transactions.getQuantity())))")
    TransactionsResponseDto toDto(Transactions transactions);

}
