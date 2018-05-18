package com.zoctan.api.controller.web;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.model.Contest;
import com.zoctan.api.service.ContestService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zoctan
 */
@RestController
@RequestMapping("/contest")
public class ContestController {
    @Resource
    private ContestService contestService;

    @PostMapping
    public Result add(@RequestBody final Contest contest) {
        this.contestService.save(contest);
        return ResultGenerator.genOkResult();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable final Long id) {
        this.contestService.deleteById(id);
        return ResultGenerator.genOkResult();
    }

    @PutMapping
    public Result update(@RequestBody final Contest contest) {
        this.contestService.update(contest);
        return ResultGenerator.genOkResult();
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable final Long id) {
        final Contest contest = this.contestService.findById(id);
        return ResultGenerator.genOkResult(contest);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") final Integer page,
                       @RequestParam(defaultValue = "0") final Integer size) {
        PageHelper.startPage(page, size);
        final List<Contest> contests = this.contestService.findAll();
        final List<Contest> list = new ArrayList<>();
        for (final Contest contest : contests) {
            contest.setDescription(null);
            list.add(contest);
        }
        final PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genOkResult(pageInfo);
    }
}
