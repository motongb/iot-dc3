/*
 * Copyright 2019 Pnoker. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pnoker.device.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pnoker.common.constant.Common;
import com.pnoker.common.dto.LabelBindDto;
import com.pnoker.common.model.LabelBind;
import com.pnoker.device.manager.mapper.LabelBindMapper;
import com.pnoker.device.manager.service.LabelBindService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * <p>标签绑定服务接口实现类
 *
 * @author pnoker
 */
@Slf4j
@Service
public class LabelBindServiceImpl implements LabelBindService {

    @Resource
    private LabelBindMapper labelBindMapper;

    @Override
    @Caching(
            put = {@CachePut(value = Common.Cache.LABEL_BIND_ID, key = "#labelBind.id", condition = "#result!=null")},
            evict = {@CacheEvict(value = Common.Cache.LABEL_BIND_LIST, allEntries = true, condition = "#result!=null")}
    )
    public LabelBind add(LabelBind labelBind) {
        if (labelBindMapper.insert(labelBind) > 0) {
            return labelBindMapper.selectById(labelBind.getId());
        }
        return null;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.LABEL_BIND_ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.LABEL_BIND_LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        return labelBindMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {@CachePut(value = Common.Cache.LABEL_BIND_ID, key = "#labelBind.id", condition = "#result!=null")},
            evict = {@CacheEvict(value = Common.Cache.LABEL_BIND_LIST, allEntries = true, condition = "#result!=null")}
    )
    public LabelBind update(LabelBind labelBind) {
        labelBind.setUpdateTime(null);
        if (labelBindMapper.updateById(labelBind) > 0) {
            return selectById(labelBind.getId());
        }
        return null;
    }

    @Override
    @Cacheable(value = Common.Cache.LABEL_BIND_ID, key = "#id", unless = "#result==null")
    public LabelBind selectById(Long id) {
        return labelBindMapper.selectById(id);
    }

    @Override
    @Cacheable(value = Common.Cache.LABEL_BIND_LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<LabelBind> list(LabelBindDto labelBindDto) {
        return labelBindMapper.selectPage(labelBindDto.getPage().convert(), fuzzyQuery(labelBindDto));
    }

    @Override
    public LambdaQueryWrapper<LabelBind> fuzzyQuery(LabelBindDto labelBindDto) {
        LambdaQueryWrapper<LabelBind> queryWrapper = Wrappers.<LabelBind>query().lambda();
        Optional.ofNullable(labelBindDto).ifPresent(dto -> {
            queryWrapper.eq(LabelBind::getLabelId, dto.getLabelId());
            queryWrapper.eq(LabelBind::getEntityId, dto.getEntityId());
            if (StringUtils.isNotBlank(dto.getType())) {
                queryWrapper.eq(LabelBind::getType, dto.getType());
            }
        });
        return queryWrapper;
    }

}