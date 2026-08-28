package shop.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.DuplicateResourceException;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.BankRequest;
import shop.backend.dto.response.BankResponse;
import shop.backend.entity.Bank;
import shop.backend.mapper.BankMapper;
import shop.backend.repository.BankRepository;
import shop.backend.service.BankService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final BankMapper bankMapper;

    @Override
    public BankResponse create(BankRequest request) {
        boolean exists = bankRepository.findAll().stream()
                .anyMatch(b -> b.getName().equalsIgnoreCase(request.getName()));
        if (exists) {
            throw new DuplicateResourceException("Bank '" + request.getName() + "' already exists");
        }
        Bank bank = Bank.builder()
                .name(request.getName())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .build();
        return bankMapper.toResponse(bankRepository.save(bank));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankResponse> getAll(boolean activeOnly) {
        List<Bank> banks = activeOnly ? bankRepository.findByIsActiveTrue() : bankRepository.findAll();
        return banks.stream().map(bankMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deactivate(Long id) {
        Bank bank = bankRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Bank", id));
        bank.setIsActive(false);
        bankRepository.save(bank);
    }
}