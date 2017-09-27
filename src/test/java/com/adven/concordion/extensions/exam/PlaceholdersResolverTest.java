package com.adven.concordion.extensions.exam;

import com.adven.concordion.extensions.exam.db.Range;
import org.concordion.api.Evaluator;
import org.joda.time.Period;
import org.junit.Test;

import java.util.Date;

import static com.adven.concordion.extensions.exam.PlaceholdersResolver.resolveJson;
import static com.adven.concordion.extensions.exam.PlaceholdersResolver.resolveToObj;
import static org.hamcrest.Matchers.is;
import static org.joda.time.LocalDateTime.fromDateFields;
import static org.joda.time.LocalDateTime.now;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlaceholdersResolverTest {
    Evaluator eval = mock(Evaluator.class);

    @Test
    public void rangeAscent() throws Exception {
        Range actual = (Range) resolveToObj("1..5", eval);
        assertThat(actual.get(0), is(1));
        assertThat(actual.get(2), is(3));
        assertThat(actual.get(5), is(1));
    }

    @Test
    public void rangeDescent() throws Exception {
        Range actual = (Range) resolveToObj("5..1", eval);
        assertThat(actual.get(0), is(5));
        assertThat(actual.get(2), is(3));
        assertThat(actual.get(5), is(5));
    }

    @Test
    public void canUseConcordionVars() throws Exception {
        when(eval.getVariable("#value")).thenReturn(3);

        assertThat(resolveJson("${#value}", eval), is("3"));
    }

    @Test
    public void canFormatConcordionVars() throws Exception {
        Date date = new Date();
        when(eval.getVariable("#value")).thenReturn(date);

        String expected = forPattern("dd.MM.yyyy HH:mm:ss").print(fromDateFields(date));
        assertThat(resolveJson("${#value:dd.MM.yyyy HH:mm:ss}", eval), is(expected));
    }

    @Test
    public void resolveToObj_shouldResolveFormattedConcordionVarToString() throws Exception {
        Date date = new Date();
        when(eval.getVariable("#value")).thenReturn(date);

        String expected = forPattern("dd.MM.yyyy HH:mm:ss").print(fromDateFields(date));
        assertThat(resolveToObj("${#value:dd.MM.yyyy HH:mm:ss}", eval).toString(), is(expected));
    }

    @Test
    public void canUseJsonUnitStringAliases() throws Exception {
        String expected = "${json-unit.any-string}";
        assertThat(resolveJson("!{any-string}", eval), is(expected));
        assertThat(resolveJson("!{aNy-stRiNG}", eval), is(expected));
        assertThat(resolveJson("!{string}", eval), is(expected));
        assertThat(resolveJson("!{str}", eval), is(expected));
    }

    @Test
    public void canUseJsonUnitNumberAliases() throws Exception {
        String expected = "${json-unit.any-number}";
        assertThat(resolveJson("!{any-number}", eval), is(expected));
        assertThat(resolveJson("!{aNy-nuMBeR}", eval), is(expected));
        assertThat(resolveJson("!{number}", eval), is(expected));
        assertThat(resolveJson("!{num}", eval), is(expected));
    }

    @Test
    public void canUseJsonUnitBoolAliases() throws Exception {
        String expected = "${json-unit.any-boolean}";
        assertThat(resolveJson("!{any-boolean}", eval), is(expected));
        assertThat(resolveJson("!{aNy-bOOlean}", eval), is(expected));
        assertThat(resolveJson("!{boolean}", eval), is(expected));
        assertThat(resolveJson("!{bool}", eval), is(expected));
    }

    @Test
    public void canUseJsonUnitMatcherAliases() throws Exception {
        assertThat(resolveJson("!{formattedAs dd.MM.yyyy}", eval),
                is("${json-unit.matches:formattedAs}dd.MM.yyyy"));
        assertThat(resolveJson("!{formattedAndWithin [yyyy-MM-dd][1d][1951-05-13]}", eval),
                is("${json-unit.matches:formattedAndWithin}[yyyy-MM-dd][1d][1951-05-13]"));
    }

    @Test
    public void canAddSimplePeriodToNow() throws Exception {
        String expected = now().plusDays(1).toString("dd.MM.yyyy");

        assertThat(resolveJson("${exam.now+[1 d]:dd.MM.yyyy}", eval), is(expected));
        assertThat(resolveJson("${exam.now+[1 day]:dd.MM.yyyy}", eval), is(expected));
        assertThat(resolveJson("${exam.now+[day 1]:dd.MM.yyyy}", eval), is(expected));
        assertThat(resolveJson("${exam.now+[1 days]:dd.MM.yyyy}", eval), is(expected));
    }

    @Test
    public void canAddCompositePeriodsToNow() throws Exception {
        assertThat(
                resolveJson(
                        "${exam.now+[1 day, 1 month]:dd.MM.yyyy}", eval
                ), is(
                        now().plus(new Period().
                                plusDays(1).plusMonths(1)).toString("dd.MM.yyyy")
                )
        );
        assertThat(
                resolveJson(
                        "${exam.now+[days 3, months 3, 3 years]:dd.MM.yyyy}", eval
                ), is(
                        now().plus(new Period().
                                plusDays(3).plusMonths(3).plusYears(3)).toString("dd.MM.yyyy")
                )
        );
        assertThat(
                resolveJson(
                        "${exam.now+[4 d, 4 M, y 4, 4 hours]:dd.MM.yyyy'T'hh}", eval
                ), is(
                        now().plus(new Period().
                                plusDays(4).plusMonths(4).plusYears(4).plusHours(4)).toString("dd.MM.yyyy'T'hh")
                )
        );
    }

    @Test
    public void canSubtractCompositePeriodsFromNow() throws Exception {
        assertThat(
                resolveJson(
                        "${exam.now-[1 day, 1 month]:dd.MM.yyyy}", eval
                ), is(
                        now().minusDays(1).minusMonths(1).toString("dd.MM.yyyy")
                )
        );
        assertThat(
                resolveJson(
                        "${exam.now-[days 3, months 3, 3 years]:dd.MM.yyyy}", eval
                ), is(
                        now().minusDays(3).minusMonths(3).minusYears(3).toString("dd.MM.yyyy")
                )
        );
        assertThat(
                resolveJson(
                        "${exam.now-[4 d, 4 M, y 4, 4 hours]:dd.MM.yyyy'T'hh}", eval
                ), is(
                        now().minusDays(4).minusMonths(4).minusYears(4).minusHours(4).toString("dd.MM.yyyy'T'hh")
                )
        );
    }
}