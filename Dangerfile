#ref https://qiita.com/kafumi/items/1a9f59d6f845808604df

# Ignore inline messages which lay outside a diff's range of PR
github.dismiss_out_of_range_messages

# Make it more obvious that a PR is a work in progress and shouldn't be merged yet
warn("PR is classed as Work in Progress") if github.pr_title.include? "[WIP]"

# ktlint
checkstyle_format.base_path = Dir.pwd
checkstyle_format.report 'app/build/reports/ktlint/ktlintMainCheck.xml'
checkstyle_format.report 'app/build/reports/ktlint/ktlintTestCheck.xml'
