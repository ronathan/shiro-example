package com.example

import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.SecurityUtils

class SignupController {
    def shiroSecurityService

    def index() {
        User user = new User()
        [user: user]
    }

    def register() {

        // Check to see if the username already exists
        def user = User.findByUsername(params.username)
        if (user) {
            flash.message = "User already exists with the username '${params.username}'"
            render(view: 'index')
        }

        // User doesn't exist with username. Let's create one
        else {

            // Make sure the passwords match
            if (params.password != params.password2) {
                flash.message = "Passwords do not match"
                render(view: 'index')
            }

            // Passwords match. Let's attempt to save the user
            else {
                // Create user
                user = new User(
                        username: params.username,
                        passwordHash: shiroSecurityService.encodePassword(params.password)
                )

                if (user.save()) {

                    // Add USER role to new user
                    user.addToRoles(Role.findByName('ROLE_USER'))

                    // Login user
                    def authToken = new UsernamePasswordToken(user.username, params.password)
                    SecurityUtils.subject.login(authToken)

                    redirect(controller: 'home', action: 'secured')
                }
                else {

                }
            }
        }
    }
}