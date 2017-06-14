package com.itss.shops.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.itss.shops.common.exception.BadRequestException;
import com.itss.shops.common.model.ListResponse;
import com.itss.shops.common.utils.MPBeanUtils;
import com.itss.shops.repository.AccountRepository;
import com.itss.shops.repository.RoleRepository;
import com.itss.shops.repository.predicate.AccountPredicate;
import com.itss.shops.service.AccountService;
import com.itss.shops.vo.request.AccountRequestVo;
import com.itss.shops.vo.response.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Predicate;
import com.itss.shops.common.utils.MPUtils;
import com.itss.shops.entity.Account;
import com.itss.shops.entity.Role;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	public Optional<Account> getAccountByLoginId(String loginId) {
		return accountRepository.findByLoginId(loginId);
	}
	
	@Override
	public ListResponse<AccountResponse> getUsers(int pageNum, int pageSize, String sortBy, String sortOrder, Boolean isShowInactive,
                                                  String searchText) {

		PageRequest pageRequest = MPUtils.getPageRequest(pageNum, pageSize, sortBy, sortOrder);
		Predicate where = AccountPredicate.findUser(searchText, isShowInactive);
		Page<Account> listUser = accountRepository.findAll(where, pageRequest);

		List<Account> users = listUser.getContent();
		List<AccountResponse> userResponses = new ArrayList<>();
		if (listUser.getTotalElements() > 0) {
			for (Account user : users) {
				AccountResponse userResponse = new AccountResponse(user);
				    userResponses.add(userResponse);
			}
		}

		ListResponse<AccountResponse> response = new ListResponse<>();
		response.setList(userResponses);
		response.setTotalCount(listUser.getTotalElements());

		return response;
	}
	
	@Override
	public Integer addAccount(AccountRequestVo accountRequest) {
		Account newAccount = new Account();
		Account accountCheck;
		if (accountRequest.getRoleId() != null && accountRequest.getRoleId() > 0) {
			Role role = roleRepository.findById((Integer) accountRequest.getRoleId());

			if (role == null)
				throw new BadRequestException("Role is invalid");

			newAccount.setRole(role);
		}
		accountCheck = accountRepository.findByUserName(accountRequest.getUserName().trim());
		if(accountCheck != null) {
			throw new BadRequestException("Account existed");
		}
		MPBeanUtils.copyProperties(accountRequest, newAccount, "id");
		try {
			accountRepository.save(newAccount);
		} catch(Exception ex) {
			throw new BadRequestException("Can not save account");
		}
		return newAccount.getId();
	}

}
